package com.romix.feature.feat639

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat639Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat639UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat639FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat639UserSummary
)

data class Feat639UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat639NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat639Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat639Config = Feat639Config()
) {

    fun loadSnapshot(userId: Long): Feat639NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat639NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat639UserSummary {
        return Feat639UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat639FeedItem> {
        val result = java.util.ArrayList<Feat639FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat639FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat639UiMapper {

    fun mapToUi(model: List<Feat639FeedItem>): Feat639UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat639UiModel(
            header = UiText("Feat639 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat639UiModel =
        Feat639UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat639UiModel =
        Feat639UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat639UiModel =
        Feat639UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat639Service(
    private val repository: Feat639Repository,
    private val uiMapper: Feat639UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat639UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat639UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat639UserItem1(val user: CoreUser, val label: String)
data class Feat639UserItem2(val user: CoreUser, val label: String)
data class Feat639UserItem3(val user: CoreUser, val label: String)
data class Feat639UserItem4(val user: CoreUser, val label: String)
data class Feat639UserItem5(val user: CoreUser, val label: String)
data class Feat639UserItem6(val user: CoreUser, val label: String)
data class Feat639UserItem7(val user: CoreUser, val label: String)
data class Feat639UserItem8(val user: CoreUser, val label: String)
data class Feat639UserItem9(val user: CoreUser, val label: String)
data class Feat639UserItem10(val user: CoreUser, val label: String)

data class Feat639StateBlock1(val state: Feat639UiModel, val checksum: Int)
data class Feat639StateBlock2(val state: Feat639UiModel, val checksum: Int)
data class Feat639StateBlock3(val state: Feat639UiModel, val checksum: Int)
data class Feat639StateBlock4(val state: Feat639UiModel, val checksum: Int)
data class Feat639StateBlock5(val state: Feat639UiModel, val checksum: Int)
data class Feat639StateBlock6(val state: Feat639UiModel, val checksum: Int)
data class Feat639StateBlock7(val state: Feat639UiModel, val checksum: Int)
data class Feat639StateBlock8(val state: Feat639UiModel, val checksum: Int)
data class Feat639StateBlock9(val state: Feat639UiModel, val checksum: Int)
data class Feat639StateBlock10(val state: Feat639UiModel, val checksum: Int)

fun buildFeat639UserItem(user: CoreUser, index: Int): Feat639UserItem1 {
    return Feat639UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat639StateBlock(model: Feat639UiModel): Feat639StateBlock1 {
    return Feat639StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat639UserSummary> {
    val list = java.util.ArrayList<Feat639UserSummary>(users.size)
    for (user in users) {
        list += Feat639UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat639UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat639UiModel {
    val summaries = (0 until count).map {
        Feat639UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat639UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat639UiModel> {
    val models = java.util.ArrayList<Feat639UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat639AnalyticsEvent1(val name: String, val value: String)
data class Feat639AnalyticsEvent2(val name: String, val value: String)
data class Feat639AnalyticsEvent3(val name: String, val value: String)
data class Feat639AnalyticsEvent4(val name: String, val value: String)
data class Feat639AnalyticsEvent5(val name: String, val value: String)
data class Feat639AnalyticsEvent6(val name: String, val value: String)
data class Feat639AnalyticsEvent7(val name: String, val value: String)
data class Feat639AnalyticsEvent8(val name: String, val value: String)
data class Feat639AnalyticsEvent9(val name: String, val value: String)
data class Feat639AnalyticsEvent10(val name: String, val value: String)

fun logFeat639Event1(event: Feat639AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat639Event2(event: Feat639AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat639Event3(event: Feat639AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat639Event4(event: Feat639AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat639Event5(event: Feat639AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat639Event6(event: Feat639AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat639Event7(event: Feat639AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat639Event8(event: Feat639AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat639Event9(event: Feat639AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat639Event10(event: Feat639AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat639Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat639Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat639Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat639Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat639Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat639Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat639Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat639Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat639Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat639Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat639(u: CoreUser): Feat639Projection1 =
    Feat639Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat639Projection1> {
    val list = java.util.ArrayList<Feat639Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat639(u)
    }
    return list
}
