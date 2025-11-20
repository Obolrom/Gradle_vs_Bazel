package com.romix.feature.feat386

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat386Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat386UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat386FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat386UserSummary
)

data class Feat386UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat386NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat386Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat386Config = Feat386Config()
) {

    fun loadSnapshot(userId: Long): Feat386NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat386NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat386UserSummary {
        return Feat386UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat386FeedItem> {
        val result = java.util.ArrayList<Feat386FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat386FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat386UiMapper {

    fun mapToUi(model: List<Feat386FeedItem>): Feat386UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat386UiModel(
            header = UiText("Feat386 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat386UiModel =
        Feat386UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat386UiModel =
        Feat386UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat386UiModel =
        Feat386UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat386Service(
    private val repository: Feat386Repository,
    private val uiMapper: Feat386UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat386UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat386UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat386UserItem1(val user: CoreUser, val label: String)
data class Feat386UserItem2(val user: CoreUser, val label: String)
data class Feat386UserItem3(val user: CoreUser, val label: String)
data class Feat386UserItem4(val user: CoreUser, val label: String)
data class Feat386UserItem5(val user: CoreUser, val label: String)
data class Feat386UserItem6(val user: CoreUser, val label: String)
data class Feat386UserItem7(val user: CoreUser, val label: String)
data class Feat386UserItem8(val user: CoreUser, val label: String)
data class Feat386UserItem9(val user: CoreUser, val label: String)
data class Feat386UserItem10(val user: CoreUser, val label: String)

data class Feat386StateBlock1(val state: Feat386UiModel, val checksum: Int)
data class Feat386StateBlock2(val state: Feat386UiModel, val checksum: Int)
data class Feat386StateBlock3(val state: Feat386UiModel, val checksum: Int)
data class Feat386StateBlock4(val state: Feat386UiModel, val checksum: Int)
data class Feat386StateBlock5(val state: Feat386UiModel, val checksum: Int)
data class Feat386StateBlock6(val state: Feat386UiModel, val checksum: Int)
data class Feat386StateBlock7(val state: Feat386UiModel, val checksum: Int)
data class Feat386StateBlock8(val state: Feat386UiModel, val checksum: Int)
data class Feat386StateBlock9(val state: Feat386UiModel, val checksum: Int)
data class Feat386StateBlock10(val state: Feat386UiModel, val checksum: Int)

fun buildFeat386UserItem(user: CoreUser, index: Int): Feat386UserItem1 {
    return Feat386UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat386StateBlock(model: Feat386UiModel): Feat386StateBlock1 {
    return Feat386StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat386UserSummary> {
    val list = java.util.ArrayList<Feat386UserSummary>(users.size)
    for (user in users) {
        list += Feat386UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat386UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat386UiModel {
    val summaries = (0 until count).map {
        Feat386UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat386UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat386UiModel> {
    val models = java.util.ArrayList<Feat386UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat386AnalyticsEvent1(val name: String, val value: String)
data class Feat386AnalyticsEvent2(val name: String, val value: String)
data class Feat386AnalyticsEvent3(val name: String, val value: String)
data class Feat386AnalyticsEvent4(val name: String, val value: String)
data class Feat386AnalyticsEvent5(val name: String, val value: String)
data class Feat386AnalyticsEvent6(val name: String, val value: String)
data class Feat386AnalyticsEvent7(val name: String, val value: String)
data class Feat386AnalyticsEvent8(val name: String, val value: String)
data class Feat386AnalyticsEvent9(val name: String, val value: String)
data class Feat386AnalyticsEvent10(val name: String, val value: String)

fun logFeat386Event1(event: Feat386AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat386Event2(event: Feat386AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat386Event3(event: Feat386AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat386Event4(event: Feat386AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat386Event5(event: Feat386AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat386Event6(event: Feat386AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat386Event7(event: Feat386AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat386Event8(event: Feat386AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat386Event9(event: Feat386AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat386Event10(event: Feat386AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat386Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat386Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat386Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat386Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat386Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat386Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat386Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat386Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat386Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat386Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat386(u: CoreUser): Feat386Projection1 =
    Feat386Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat386Projection1> {
    val list = java.util.ArrayList<Feat386Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat386(u)
    }
    return list
}
