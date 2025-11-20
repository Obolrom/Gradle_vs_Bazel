package com.romix.feature.feat52

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat52Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat52UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat52FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat52UserSummary
)

data class Feat52UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat52NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat52Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat52Config = Feat52Config()
) {

    fun loadSnapshot(userId: Long): Feat52NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat52NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat52UserSummary {
        return Feat52UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat52FeedItem> {
        val result = java.util.ArrayList<Feat52FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat52FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat52UiMapper {

    fun mapToUi(model: List<Feat52FeedItem>): Feat52UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat52UiModel(
            header = UiText("Feat52 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat52UiModel =
        Feat52UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat52UiModel =
        Feat52UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat52UiModel =
        Feat52UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat52Service(
    private val repository: Feat52Repository,
    private val uiMapper: Feat52UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat52UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat52UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat52UserItem1(val user: CoreUser, val label: String)
data class Feat52UserItem2(val user: CoreUser, val label: String)
data class Feat52UserItem3(val user: CoreUser, val label: String)
data class Feat52UserItem4(val user: CoreUser, val label: String)
data class Feat52UserItem5(val user: CoreUser, val label: String)
data class Feat52UserItem6(val user: CoreUser, val label: String)
data class Feat52UserItem7(val user: CoreUser, val label: String)
data class Feat52UserItem8(val user: CoreUser, val label: String)
data class Feat52UserItem9(val user: CoreUser, val label: String)
data class Feat52UserItem10(val user: CoreUser, val label: String)

data class Feat52StateBlock1(val state: Feat52UiModel, val checksum: Int)
data class Feat52StateBlock2(val state: Feat52UiModel, val checksum: Int)
data class Feat52StateBlock3(val state: Feat52UiModel, val checksum: Int)
data class Feat52StateBlock4(val state: Feat52UiModel, val checksum: Int)
data class Feat52StateBlock5(val state: Feat52UiModel, val checksum: Int)
data class Feat52StateBlock6(val state: Feat52UiModel, val checksum: Int)
data class Feat52StateBlock7(val state: Feat52UiModel, val checksum: Int)
data class Feat52StateBlock8(val state: Feat52UiModel, val checksum: Int)
data class Feat52StateBlock9(val state: Feat52UiModel, val checksum: Int)
data class Feat52StateBlock10(val state: Feat52UiModel, val checksum: Int)

fun buildFeat52UserItem(user: CoreUser, index: Int): Feat52UserItem1 {
    return Feat52UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat52StateBlock(model: Feat52UiModel): Feat52StateBlock1 {
    return Feat52StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat52UserSummary> {
    val list = java.util.ArrayList<Feat52UserSummary>(users.size)
    for (user in users) {
        list += Feat52UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat52UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat52UiModel {
    val summaries = (0 until count).map {
        Feat52UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat52UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat52UiModel> {
    val models = java.util.ArrayList<Feat52UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat52AnalyticsEvent1(val name: String, val value: String)
data class Feat52AnalyticsEvent2(val name: String, val value: String)
data class Feat52AnalyticsEvent3(val name: String, val value: String)
data class Feat52AnalyticsEvent4(val name: String, val value: String)
data class Feat52AnalyticsEvent5(val name: String, val value: String)
data class Feat52AnalyticsEvent6(val name: String, val value: String)
data class Feat52AnalyticsEvent7(val name: String, val value: String)
data class Feat52AnalyticsEvent8(val name: String, val value: String)
data class Feat52AnalyticsEvent9(val name: String, val value: String)
data class Feat52AnalyticsEvent10(val name: String, val value: String)

fun logFeat52Event1(event: Feat52AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat52Event2(event: Feat52AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat52Event3(event: Feat52AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat52Event4(event: Feat52AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat52Event5(event: Feat52AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat52Event6(event: Feat52AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat52Event7(event: Feat52AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat52Event8(event: Feat52AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat52Event9(event: Feat52AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat52Event10(event: Feat52AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat52Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat52Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat52Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat52Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat52Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat52Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat52Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat52Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat52Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat52Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat52(u: CoreUser): Feat52Projection1 =
    Feat52Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat52Projection1> {
    val list = java.util.ArrayList<Feat52Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat52(u)
    }
    return list
}
