package com.romix.feature.feat604

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat604Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat604UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat604FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat604UserSummary
)

data class Feat604UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat604NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat604Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat604Config = Feat604Config()
) {

    fun loadSnapshot(userId: Long): Feat604NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat604NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat604UserSummary {
        return Feat604UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat604FeedItem> {
        val result = java.util.ArrayList<Feat604FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat604FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat604UiMapper {

    fun mapToUi(model: List<Feat604FeedItem>): Feat604UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat604UiModel(
            header = UiText("Feat604 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat604UiModel =
        Feat604UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat604UiModel =
        Feat604UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat604UiModel =
        Feat604UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat604Service(
    private val repository: Feat604Repository,
    private val uiMapper: Feat604UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat604UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat604UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat604UserItem1(val user: CoreUser, val label: String)
data class Feat604UserItem2(val user: CoreUser, val label: String)
data class Feat604UserItem3(val user: CoreUser, val label: String)
data class Feat604UserItem4(val user: CoreUser, val label: String)
data class Feat604UserItem5(val user: CoreUser, val label: String)
data class Feat604UserItem6(val user: CoreUser, val label: String)
data class Feat604UserItem7(val user: CoreUser, val label: String)
data class Feat604UserItem8(val user: CoreUser, val label: String)
data class Feat604UserItem9(val user: CoreUser, val label: String)
data class Feat604UserItem10(val user: CoreUser, val label: String)

data class Feat604StateBlock1(val state: Feat604UiModel, val checksum: Int)
data class Feat604StateBlock2(val state: Feat604UiModel, val checksum: Int)
data class Feat604StateBlock3(val state: Feat604UiModel, val checksum: Int)
data class Feat604StateBlock4(val state: Feat604UiModel, val checksum: Int)
data class Feat604StateBlock5(val state: Feat604UiModel, val checksum: Int)
data class Feat604StateBlock6(val state: Feat604UiModel, val checksum: Int)
data class Feat604StateBlock7(val state: Feat604UiModel, val checksum: Int)
data class Feat604StateBlock8(val state: Feat604UiModel, val checksum: Int)
data class Feat604StateBlock9(val state: Feat604UiModel, val checksum: Int)
data class Feat604StateBlock10(val state: Feat604UiModel, val checksum: Int)

fun buildFeat604UserItem(user: CoreUser, index: Int): Feat604UserItem1 {
    return Feat604UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat604StateBlock(model: Feat604UiModel): Feat604StateBlock1 {
    return Feat604StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat604UserSummary> {
    val list = java.util.ArrayList<Feat604UserSummary>(users.size)
    for (user in users) {
        list += Feat604UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat604UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat604UiModel {
    val summaries = (0 until count).map {
        Feat604UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat604UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat604UiModel> {
    val models = java.util.ArrayList<Feat604UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat604AnalyticsEvent1(val name: String, val value: String)
data class Feat604AnalyticsEvent2(val name: String, val value: String)
data class Feat604AnalyticsEvent3(val name: String, val value: String)
data class Feat604AnalyticsEvent4(val name: String, val value: String)
data class Feat604AnalyticsEvent5(val name: String, val value: String)
data class Feat604AnalyticsEvent6(val name: String, val value: String)
data class Feat604AnalyticsEvent7(val name: String, val value: String)
data class Feat604AnalyticsEvent8(val name: String, val value: String)
data class Feat604AnalyticsEvent9(val name: String, val value: String)
data class Feat604AnalyticsEvent10(val name: String, val value: String)

fun logFeat604Event1(event: Feat604AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat604Event2(event: Feat604AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat604Event3(event: Feat604AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat604Event4(event: Feat604AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat604Event5(event: Feat604AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat604Event6(event: Feat604AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat604Event7(event: Feat604AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat604Event8(event: Feat604AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat604Event9(event: Feat604AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat604Event10(event: Feat604AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat604Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat604Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat604Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat604Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat604Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat604Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat604Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat604Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat604Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat604Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat604(u: CoreUser): Feat604Projection1 =
    Feat604Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat604Projection1> {
    val list = java.util.ArrayList<Feat604Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat604(u)
    }
    return list
}
