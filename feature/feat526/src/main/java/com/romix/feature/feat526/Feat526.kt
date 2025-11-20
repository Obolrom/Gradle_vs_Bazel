package com.romix.feature.feat526

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat526Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat526UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat526FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat526UserSummary
)

data class Feat526UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat526NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat526Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat526Config = Feat526Config()
) {

    fun loadSnapshot(userId: Long): Feat526NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat526NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat526UserSummary {
        return Feat526UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat526FeedItem> {
        val result = java.util.ArrayList<Feat526FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat526FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat526UiMapper {

    fun mapToUi(model: List<Feat526FeedItem>): Feat526UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat526UiModel(
            header = UiText("Feat526 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat526UiModel =
        Feat526UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat526UiModel =
        Feat526UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat526UiModel =
        Feat526UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat526Service(
    private val repository: Feat526Repository,
    private val uiMapper: Feat526UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat526UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat526UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat526UserItem1(val user: CoreUser, val label: String)
data class Feat526UserItem2(val user: CoreUser, val label: String)
data class Feat526UserItem3(val user: CoreUser, val label: String)
data class Feat526UserItem4(val user: CoreUser, val label: String)
data class Feat526UserItem5(val user: CoreUser, val label: String)
data class Feat526UserItem6(val user: CoreUser, val label: String)
data class Feat526UserItem7(val user: CoreUser, val label: String)
data class Feat526UserItem8(val user: CoreUser, val label: String)
data class Feat526UserItem9(val user: CoreUser, val label: String)
data class Feat526UserItem10(val user: CoreUser, val label: String)

data class Feat526StateBlock1(val state: Feat526UiModel, val checksum: Int)
data class Feat526StateBlock2(val state: Feat526UiModel, val checksum: Int)
data class Feat526StateBlock3(val state: Feat526UiModel, val checksum: Int)
data class Feat526StateBlock4(val state: Feat526UiModel, val checksum: Int)
data class Feat526StateBlock5(val state: Feat526UiModel, val checksum: Int)
data class Feat526StateBlock6(val state: Feat526UiModel, val checksum: Int)
data class Feat526StateBlock7(val state: Feat526UiModel, val checksum: Int)
data class Feat526StateBlock8(val state: Feat526UiModel, val checksum: Int)
data class Feat526StateBlock9(val state: Feat526UiModel, val checksum: Int)
data class Feat526StateBlock10(val state: Feat526UiModel, val checksum: Int)

fun buildFeat526UserItem(user: CoreUser, index: Int): Feat526UserItem1 {
    return Feat526UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat526StateBlock(model: Feat526UiModel): Feat526StateBlock1 {
    return Feat526StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat526UserSummary> {
    val list = java.util.ArrayList<Feat526UserSummary>(users.size)
    for (user in users) {
        list += Feat526UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat526UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat526UiModel {
    val summaries = (0 until count).map {
        Feat526UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat526UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat526UiModel> {
    val models = java.util.ArrayList<Feat526UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat526AnalyticsEvent1(val name: String, val value: String)
data class Feat526AnalyticsEvent2(val name: String, val value: String)
data class Feat526AnalyticsEvent3(val name: String, val value: String)
data class Feat526AnalyticsEvent4(val name: String, val value: String)
data class Feat526AnalyticsEvent5(val name: String, val value: String)
data class Feat526AnalyticsEvent6(val name: String, val value: String)
data class Feat526AnalyticsEvent7(val name: String, val value: String)
data class Feat526AnalyticsEvent8(val name: String, val value: String)
data class Feat526AnalyticsEvent9(val name: String, val value: String)
data class Feat526AnalyticsEvent10(val name: String, val value: String)

fun logFeat526Event1(event: Feat526AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat526Event2(event: Feat526AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat526Event3(event: Feat526AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat526Event4(event: Feat526AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat526Event5(event: Feat526AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat526Event6(event: Feat526AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat526Event7(event: Feat526AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat526Event8(event: Feat526AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat526Event9(event: Feat526AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat526Event10(event: Feat526AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat526Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat526Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat526Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat526Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat526Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat526Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat526Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat526Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat526Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat526Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat526(u: CoreUser): Feat526Projection1 =
    Feat526Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat526Projection1> {
    val list = java.util.ArrayList<Feat526Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat526(u)
    }
    return list
}
