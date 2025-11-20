package com.romix.feature.feat186

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat186Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat186UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat186FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat186UserSummary
)

data class Feat186UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat186NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat186Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat186Config = Feat186Config()
) {

    fun loadSnapshot(userId: Long): Feat186NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat186NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat186UserSummary {
        return Feat186UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat186FeedItem> {
        val result = java.util.ArrayList<Feat186FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat186FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat186UiMapper {

    fun mapToUi(model: List<Feat186FeedItem>): Feat186UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat186UiModel(
            header = UiText("Feat186 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat186UiModel =
        Feat186UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat186UiModel =
        Feat186UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat186UiModel =
        Feat186UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat186Service(
    private val repository: Feat186Repository,
    private val uiMapper: Feat186UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat186UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat186UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat186UserItem1(val user: CoreUser, val label: String)
data class Feat186UserItem2(val user: CoreUser, val label: String)
data class Feat186UserItem3(val user: CoreUser, val label: String)
data class Feat186UserItem4(val user: CoreUser, val label: String)
data class Feat186UserItem5(val user: CoreUser, val label: String)
data class Feat186UserItem6(val user: CoreUser, val label: String)
data class Feat186UserItem7(val user: CoreUser, val label: String)
data class Feat186UserItem8(val user: CoreUser, val label: String)
data class Feat186UserItem9(val user: CoreUser, val label: String)
data class Feat186UserItem10(val user: CoreUser, val label: String)

data class Feat186StateBlock1(val state: Feat186UiModel, val checksum: Int)
data class Feat186StateBlock2(val state: Feat186UiModel, val checksum: Int)
data class Feat186StateBlock3(val state: Feat186UiModel, val checksum: Int)
data class Feat186StateBlock4(val state: Feat186UiModel, val checksum: Int)
data class Feat186StateBlock5(val state: Feat186UiModel, val checksum: Int)
data class Feat186StateBlock6(val state: Feat186UiModel, val checksum: Int)
data class Feat186StateBlock7(val state: Feat186UiModel, val checksum: Int)
data class Feat186StateBlock8(val state: Feat186UiModel, val checksum: Int)
data class Feat186StateBlock9(val state: Feat186UiModel, val checksum: Int)
data class Feat186StateBlock10(val state: Feat186UiModel, val checksum: Int)

fun buildFeat186UserItem(user: CoreUser, index: Int): Feat186UserItem1 {
    return Feat186UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat186StateBlock(model: Feat186UiModel): Feat186StateBlock1 {
    return Feat186StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat186UserSummary> {
    val list = java.util.ArrayList<Feat186UserSummary>(users.size)
    for (user in users) {
        list += Feat186UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat186UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat186UiModel {
    val summaries = (0 until count).map {
        Feat186UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat186UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat186UiModel> {
    val models = java.util.ArrayList<Feat186UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat186AnalyticsEvent1(val name: String, val value: String)
data class Feat186AnalyticsEvent2(val name: String, val value: String)
data class Feat186AnalyticsEvent3(val name: String, val value: String)
data class Feat186AnalyticsEvent4(val name: String, val value: String)
data class Feat186AnalyticsEvent5(val name: String, val value: String)
data class Feat186AnalyticsEvent6(val name: String, val value: String)
data class Feat186AnalyticsEvent7(val name: String, val value: String)
data class Feat186AnalyticsEvent8(val name: String, val value: String)
data class Feat186AnalyticsEvent9(val name: String, val value: String)
data class Feat186AnalyticsEvent10(val name: String, val value: String)

fun logFeat186Event1(event: Feat186AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat186Event2(event: Feat186AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat186Event3(event: Feat186AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat186Event4(event: Feat186AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat186Event5(event: Feat186AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat186Event6(event: Feat186AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat186Event7(event: Feat186AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat186Event8(event: Feat186AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat186Event9(event: Feat186AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat186Event10(event: Feat186AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat186Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat186Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat186Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat186Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat186Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat186Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat186Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat186Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat186Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat186Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat186(u: CoreUser): Feat186Projection1 =
    Feat186Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat186Projection1> {
    val list = java.util.ArrayList<Feat186Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat186(u)
    }
    return list
}
