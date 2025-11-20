package com.romix.feature.feat515

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat515Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat515UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat515FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat515UserSummary
)

data class Feat515UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat515NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat515Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat515Config = Feat515Config()
) {

    fun loadSnapshot(userId: Long): Feat515NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat515NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat515UserSummary {
        return Feat515UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat515FeedItem> {
        val result = java.util.ArrayList<Feat515FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat515FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat515UiMapper {

    fun mapToUi(model: List<Feat515FeedItem>): Feat515UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat515UiModel(
            header = UiText("Feat515 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat515UiModel =
        Feat515UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat515UiModel =
        Feat515UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat515UiModel =
        Feat515UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat515Service(
    private val repository: Feat515Repository,
    private val uiMapper: Feat515UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat515UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat515UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat515UserItem1(val user: CoreUser, val label: String)
data class Feat515UserItem2(val user: CoreUser, val label: String)
data class Feat515UserItem3(val user: CoreUser, val label: String)
data class Feat515UserItem4(val user: CoreUser, val label: String)
data class Feat515UserItem5(val user: CoreUser, val label: String)
data class Feat515UserItem6(val user: CoreUser, val label: String)
data class Feat515UserItem7(val user: CoreUser, val label: String)
data class Feat515UserItem8(val user: CoreUser, val label: String)
data class Feat515UserItem9(val user: CoreUser, val label: String)
data class Feat515UserItem10(val user: CoreUser, val label: String)

data class Feat515StateBlock1(val state: Feat515UiModel, val checksum: Int)
data class Feat515StateBlock2(val state: Feat515UiModel, val checksum: Int)
data class Feat515StateBlock3(val state: Feat515UiModel, val checksum: Int)
data class Feat515StateBlock4(val state: Feat515UiModel, val checksum: Int)
data class Feat515StateBlock5(val state: Feat515UiModel, val checksum: Int)
data class Feat515StateBlock6(val state: Feat515UiModel, val checksum: Int)
data class Feat515StateBlock7(val state: Feat515UiModel, val checksum: Int)
data class Feat515StateBlock8(val state: Feat515UiModel, val checksum: Int)
data class Feat515StateBlock9(val state: Feat515UiModel, val checksum: Int)
data class Feat515StateBlock10(val state: Feat515UiModel, val checksum: Int)

fun buildFeat515UserItem(user: CoreUser, index: Int): Feat515UserItem1 {
    return Feat515UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat515StateBlock(model: Feat515UiModel): Feat515StateBlock1 {
    return Feat515StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat515UserSummary> {
    val list = java.util.ArrayList<Feat515UserSummary>(users.size)
    for (user in users) {
        list += Feat515UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat515UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat515UiModel {
    val summaries = (0 until count).map {
        Feat515UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat515UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat515UiModel> {
    val models = java.util.ArrayList<Feat515UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat515AnalyticsEvent1(val name: String, val value: String)
data class Feat515AnalyticsEvent2(val name: String, val value: String)
data class Feat515AnalyticsEvent3(val name: String, val value: String)
data class Feat515AnalyticsEvent4(val name: String, val value: String)
data class Feat515AnalyticsEvent5(val name: String, val value: String)
data class Feat515AnalyticsEvent6(val name: String, val value: String)
data class Feat515AnalyticsEvent7(val name: String, val value: String)
data class Feat515AnalyticsEvent8(val name: String, val value: String)
data class Feat515AnalyticsEvent9(val name: String, val value: String)
data class Feat515AnalyticsEvent10(val name: String, val value: String)

fun logFeat515Event1(event: Feat515AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat515Event2(event: Feat515AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat515Event3(event: Feat515AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat515Event4(event: Feat515AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat515Event5(event: Feat515AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat515Event6(event: Feat515AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat515Event7(event: Feat515AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat515Event8(event: Feat515AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat515Event9(event: Feat515AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat515Event10(event: Feat515AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat515Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat515Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat515Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat515Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat515Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat515Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat515Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat515Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat515Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat515Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat515(u: CoreUser): Feat515Projection1 =
    Feat515Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat515Projection1> {
    val list = java.util.ArrayList<Feat515Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat515(u)
    }
    return list
}
